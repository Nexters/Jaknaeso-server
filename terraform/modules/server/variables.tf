variable "region" {
  default = "KR"
}

variable "zones" {
  default = "KR-2"
}

variable "name_terra" {
  default = "tf-jaknaeso"
}

variable "login_key_name" {
  default = "jaknaeso-login-key"
}

variable "vpc_no" {
  description = "vpc number"
}

variable "subnet_public_id" {
  description = "subnet public id"
}

variable "admin_ip_cidrs" {
  description = "List of CIDR blocks to allow SSH access from"
  type = list(string)
  default = []
}