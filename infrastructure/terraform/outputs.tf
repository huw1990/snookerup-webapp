output "instance_id" {
  value       = aws_instance.app_server.id
  description = "EC2 Instance ID"
}

output "public_ip" {
  value       = aws_eip.app_eip.public_ip
  description = "Elastic Public IP of the application server"
}

output "domain_url" {
  value       = "https://${var.domain_name}"
  description = "URL of the deployed SnookerUp web app"
}