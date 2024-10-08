package Albaid.backend.global.security.rft.repository;

import Albaid.backend.global.security.rft.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
