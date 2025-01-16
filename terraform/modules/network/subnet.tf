resource "ncloud_subnet" "subnet_public" {
  name = "${var.name_terra}-public"
  vpc_no = ncloud_vpc.vpc.vpc_no
  subnet = cidrsubnet(ncloud_vpc.vpc.ipv4_cidr_block, 8, 0) // "10.0.0.0/24"
  zone = var.zones
  network_acl_no = ncloud_vpc.vpc.default_network_acl_no
  subnet_type = "PUBLIC"
}