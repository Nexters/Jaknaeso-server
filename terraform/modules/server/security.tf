resource "ncloud_login_key" "jaknaeso_server_login_key" {
  key_name = "${var.name_terra}-jaknaeso-key"
}

resource "local_file" "ncp_pem" {
  filename = "${ncloud_login_key.jaknaeso_server_login_key.key_name}.pem"
  content  = ncloud_login_key.jaknaeso_server_login_key.private_key
}

resource "local_file" "jaknaeso_server_root_password" {
  filename = "jaknaeso-server-root_password.txt"
  content  = "jaknaeso-server => ${data.ncloud_root_password.default.root_password}"
}