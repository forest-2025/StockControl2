package com.example.dto.products;

import java.util.List;

import lombok.Data;

@Data
public class UploadResult {
	
	private List<String> errors;
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
