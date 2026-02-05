package com.example.dto.products;

import com.example.dto.common.MSupplier;

import lombok.Data;

@Data
public class ProductWithSupplier {
	
	private MProduct product;
	private MSupplier supplier; 

}
