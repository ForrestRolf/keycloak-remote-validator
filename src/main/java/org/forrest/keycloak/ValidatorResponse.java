package org.forrest.keycloak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidatorResponse {
    private boolean success;
    private String message;
}
