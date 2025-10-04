package com.devsdoagi.agicripto.DTO;

import java.io.Serializable;

/* Resposta DTO será utilizada dentro de Handler */
public record ApiError(int status, String error, String message, String path, String timestamp) { }
