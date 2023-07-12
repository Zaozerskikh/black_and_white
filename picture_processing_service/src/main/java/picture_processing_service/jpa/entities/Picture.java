package picture_processing_service.jpa.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Arrays;

@Data
@NoArgsConstructor
@Entity(name = "picture")
public class Picture {
    @Id
    @Column(name = "picture_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pictureId;

    @Column(name = "link", unique = true)
    private String link;

    @Lob
    @Type(type = "org.hibernate.type.ImageType")
    private byte[] image;

    public Picture(byte[] image, String link) {
        this.image = Arrays.copyOf(image, image.length);
        this.link = link;
    }
}
