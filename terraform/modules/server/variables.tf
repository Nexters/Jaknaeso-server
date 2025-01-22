variable "region" {
  default = "KR"
}

variable "zones" {
  default = "KR-2"
}

variable "name_terra" {
  default = "tf-jaknaeso"
  sensitive = true
}

variable "login_key_name" {
  default = "jaknaeso-login-key"
  sensitive = true
}

variable "vpc_no" {
  sensitive = true
  description = "vpc number"
}

variable "subnet_public_id" {
  sensitive = true
  description = "subnet public id"
}

variable "admin_ip_cidrs" {
  description = "List of CIDR blocks to allow SSH access from"
  type = list(string)
  default = []
  sensitive = true
}