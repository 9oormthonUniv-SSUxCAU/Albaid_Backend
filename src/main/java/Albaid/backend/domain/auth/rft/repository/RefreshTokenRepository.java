package Albaid.backend.domain.auth.rft.repository;

import Albaid.backend.domain.auth.rft.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
