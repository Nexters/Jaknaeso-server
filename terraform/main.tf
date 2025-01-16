terraform {
  required_providers {
    ncloud = {
      source = "NaverCloudPlatform/ncloud"
    }
  }
  required_version = ">= 0.13"
  backend "s3" {
    bucket = "terraform-state"
    key = "terraform.tfstate"
    region = "KR"

    skip_region_validation      = true
    skip_requesting_account_id  = true
    skip_credentials_validation = true
    skip_metadata_api_check     = true
    skip_s3_checksum = true

    use_lockfile = true

    endpoint = "https://kr.object.ncloudstorage.com"
  }
}

provider "ncloud" {
  support_vpc = true
  region = var.region
  access_key = var.access_key
  secret_key = var.secret_key
}

module "network" {
  source  = "./modules/network"

}
module "server" {
  source  = "./modules/server"
  vpc_no = module.network.vpc_no
  subnet_public_id = module.network.subnet_public_id
}