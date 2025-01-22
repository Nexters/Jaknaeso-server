variable "zones" {
  default = "KR-2"
}

variable "name_terra" {
  default = "tf-jaknaeso"
  sensitive = true
}

variable "vpc_cidr" {
  default = "10.0.0.0/16"
  sensitive = true
}