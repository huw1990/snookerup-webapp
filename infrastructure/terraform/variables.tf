variable "aws_region" {
  type        = string
  default     = "eu-west-2"
  description = "AWS region for deployment"
}

variable "environment" {
  type        = string
  default     = "prod"
  description = "Environment tag"
}

variable "app_name" {
  type        = string
  default     = "snookerup"
  description = "Application name prefix"
}

variable "instance_type" {
  type        = string
  default     = "t4g.small"
  description = "ARM64 EC2 instance type (~$12/mo)"
}

variable "domain_name" {
  type        = string
  description = "Registered domain name in Route53 (e.g. snookerup.com)"
}

variable "admin_email" {
  type        = string
  description = "Admin email address for Let's Encrypt certificates"
}

variable "create_www_record" {
  type        = bool
  default     = false
  description = "Whether to create a www CNAME record"
}

variable "cognito_user_pool_id" {
  type        = string
  description = "Existing AWS Cognito User Pool ID"
}

variable "cognito_client_name" {
  type        = string
  description = "Existing AWS Cognito App Client Name"
}

variable "cognito_client_id" {
  type        = string
  description = "Existing AWS Cognito App Client ID"
}

variable "cognito_client_secret" {
  type        = string
  description = "Existing AWS Cognito App Client Secret"
}

variable "invite_codes" {
  type        = string
  description = "Invite codes for users to sign up with"
}