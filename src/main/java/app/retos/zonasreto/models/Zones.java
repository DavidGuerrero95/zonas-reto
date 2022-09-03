package app.retos.zonasreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "zona")
@Data
@NoArgsConstructor
public class Zones {

    @Id
    @JsonIgnore
    private String id;

    @Indexed(unique = true)
    private Integer zoneCode;

    @NotEmpty(message = "locacion no puedde esta vacia")
    @Size(min=2,max = 2, message = "Debe tener dos valores")
    private List<Double> location;

    private List<String> idEvents;
    private List<Integer> idPosts;

}