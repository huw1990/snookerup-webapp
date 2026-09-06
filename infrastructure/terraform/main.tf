terraform {
  required_version = ">= 1.5.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    tls = {
      source  = "hashicorp/tls"
      version = "~> 4.0"
    }
  }

  backend "s3" {
    # Placeholders replaced via backend config or CLI parameters during terraform init
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "snookerup-webapp"
      Environment = var.environment
      ManagedBy   = "Terraform"
    }
  }
}

data "aws_region" "current" {}
data "aws_caller_identity" "current" {}

# Route53 Zone Lookups
data "aws_route53_zone" "primary" {
  name         = var.domain_name
  private_zone = false
}

# --- VPC & Networking (Default VPC for cost savings) ---
data "aws_vpc" "default" {
  default = true
}

data "aws_subnets" "default" {
  filter {
    name   = "vpc-id"
    values = [data.aws_vpc.default.id]
  }
}

# Security Group
resource "aws_security_group" "ec2_sg" {
  name        = "${var.app_name}-ec2-sg"
  description = "Security group for SnookerUp web app server"
  vpc_id      = data.aws_vpc.default.id

  # HTTP
  ingress {
    description = "Allow HTTP"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS
  ingress {
    description = "Allow HTTPS"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Egress all
  egress {
    description = "Allow all outbound"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# --- S3 Bucket for Database Backups ---
resource "aws_s3_bucket" "backups" {
  bucket        = "${var.app_name}-db-backups-${data.aws_caller_identity.current.account_id}"
  force_destroy = false
}

resource "aws_s3_bucket_public_access_block" "backups_access" {
  bucket                  = aws_s3_bucket.backups.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

# --- IAM Role & Policies for EC2 Instance ---
resource "aws_iam_role" "ec2_role" {
  name = "${var.app_name}-ec2-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "ssm_policy" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_role_policy_attachment" "ecr_readonly" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
}

# IAM Policy allowing EC2 to upload backups to S3
resource "aws_iam_policy" "s3_backup_policy" {
  name        = "${var.app_name}-s3-backup-policy"
  description = "Allows EC2 to sync database backups to S3"

  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Effect = "Allow"
        Action = [
          "s3:PutObject",
          "s3:GetObject",
          "s3:ListBucket"
        ]
        Resource = [
          aws_s3_bucket.backups.arn,
          "${aws_s3_bucket.backups.arn}/*"
        ]
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "s3_backup_attach" {
  role       = aws_iam_role.ec2_role.name
  policy_arn = aws_iam_policy.s3_backup_policy.arn
}

resource "aws_iam_instance_profile" "ec2_profile" {
  name = "${var.app_name}-ec2-instance-profile"
  role = aws_iam_role.ec2_role.name
}

# AMI (Latest Ubuntu 24.04 LTS ARM64)
data "aws_ami" "ubuntu_arm64" {
  most_recent = true
  owners      = ["099720109477"] # Canonical

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd-gp3/ubuntu-noble-24.04-arm64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# User Data Template Script
data "template_file" "user_data" {
  template = file("${path.module}/user_data.sh.tpl")
  vars = {
    app_name              = var.app_name
    domain_name           = var.domain_name
    admin_email           = var.admin_email
    aws_region            = var.aws_region
    ecr_repo_uri          = "${data.aws_caller_identity.current.account_id}.dkr.ecr.${var.aws_region}.amazonaws.com/${var.app_name}-webapp"
    cognito_user_pool_id  = var.cognito_user_pool_id
    cognito_client_name   = var.cognito_client_name
    cognito_client_id     = var.cognito_client_id
    cognito_client_secret = var.cognito_client_secret
    s3_backup_bucket      = aws_s3_bucket.backups.id
    invite_codes          = var.invite_codes
  }
}

# EC2 Instance
resource "aws_instance" "app_server" {
  ami                  = data.aws_ami.ubuntu_arm64.id
  instance_type        = var.instance_type
  subnet_id            = data.aws_subnets.default.ids[0]
  vpc_security_group_ids = [aws_security_group.ec2_sg.id]
  iam_instance_profile = aws_iam_instance_profile.ec2_profile.name

  root_block_device {
    volume_size           = 20
    volume_type           = "gp3"
    encrypted             = true
    delete_on_termination = false
  }

  user_data = data.template_file.user_data.rendered

  tags = {
    Name = "${var.app_name}-server"
  }
}

# Elastic IP allocation & association
resource "aws_eip" "app_eip" {
  domain   = "vpc"
  instance = aws_instance.app_server.id

  tags = {
    Name = "${var.app_name}-eip"
  }
}

# Route53 A Record
resource "aws_route53_record" "app_domain" {
  zone_id = data.aws_route53_zone.primary.zone_id
  name    = var.domain_name
  type    = "A"
  ttl     = 300
  records = [aws_eip.app_eip.public_ip]
}

# Route53 WWW CNAME Record (optional)
resource "aws_route53_record" "app_www" {
  count   = var.create_www_record ? 1 : 0
  zone_id = data.aws_route53_zone.primary.zone_id
  name    = "www.${var.domain_name}"
  type    = "CNAME"
  ttl     = 300
  records = [var.domain_name]
}