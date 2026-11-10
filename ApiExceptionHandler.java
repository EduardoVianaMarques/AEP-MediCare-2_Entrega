package medicare.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MedicamentoNaoEncontradoException.class)
    public ResponseEntity<ApiError> handleNotFound(MedicamentoNaoEncontradoException e, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND,e.getMessage(),request.getRequestURI(),Map.of());
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException e,HttpServletRequest request){
        Map<String,String> campos=new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error->campos.putIfAbsent(error.getField(),error.getDefaultMessage()));
        return build(HttpStatus.BAD_REQUEST,"Verifique os campos informados",request.getRequestURI(),campos);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException e,HttpServletRequest request){
        return build(HttpStatus.BAD_REQUEST,e.getMessage(),request.getRequestURI(),Map.of());
    }
    @ExceptionHandler(org.springframework.dao.DuplicateKeyException.class)
    public ResponseEntity<ApiError> handleDuplicate(Exception e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, "Este email já está cadastrado.", request.getRequestURI(), Map.of());
    }
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleJson(Exception e, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "JSON inválido.", request.getRequestURI(), Map.of());
    }
    @ExceptionHandler(ConflitoEstoqueException.class)
    public ResponseEntity<ApiError> handleEstoque(ConflitoEstoqueException e, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, e.getMessage(), request.getRequestURI(), Map.of());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception e,HttpServletRequest request){
        return build(HttpStatus.INTERNAL_SERVER_ERROR,"Erro interno do servidor.",request.getRequestURI(),Map.of());
    }
    private ResponseEntity<ApiError> build(HttpStatus status,String mensagem,String caminho,Map<String,String> campos){
        return ResponseEntity.status(status).body(new ApiError(Instant.now(),status.value(),mensagem,caminho,campos));
    }
}
