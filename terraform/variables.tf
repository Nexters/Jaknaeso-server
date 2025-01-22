variable "region" {
  default = "KR"
}

variable "access_key" {
  description = "NCP access key"
  sensitive = true
}

variable "secret_key" {
  description = "NCP secret key"
  sensitive = true
}

variable "admin_ip_cidrs" {
  description = "List of CIDR blocks to allow SSH access from"
  type = list(string)
  default = []
  sensitive   = true
}