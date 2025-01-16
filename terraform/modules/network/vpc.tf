resource "ncloud_vpc" "vpc" {
  name = "${var.name_terra}-vpc"
  ipv4_cidr_block = var.vpc_cidr
}