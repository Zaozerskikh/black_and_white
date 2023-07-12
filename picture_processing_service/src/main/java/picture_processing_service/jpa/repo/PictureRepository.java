package picture_processing_service.jpa.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import picture_processing_service.jpa.entities.Picture;

import java.util.Optional;

public interface PictureRepository extends JpaRepository<Picture, Long> {
    Optional<Picture> findByLink(String link);
}
