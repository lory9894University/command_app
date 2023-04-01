package it.unito.edu.scavolini.order_management.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserTransient {

    private String userId;

    private String username;
}
