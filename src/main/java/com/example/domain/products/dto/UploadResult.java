package com.example.domain.products.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class UploadResult {
	
	private List<String> errors = new ArrayList<>();
    private String fileName; 
    
    public UploadResult() {
		
   	}

    public UploadResult(List<String> errors,String fileName) {
        this.errors = errors;
        this.fileName = fileName;
    }
    
	public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
}
