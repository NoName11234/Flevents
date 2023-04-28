package de.flyndre.fleventsbackend.repositories;

import de.flyndre.fleventsbackend.Models.OrganizationAccount;
import de.flyndre.fleventsbackend.Models.OrganizationRole;
import de.flyndre.fleventsbackend.Models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OrganizationAccountRepository extends JpaRepository<OrganizationAccount,String> {
    public List<OrganizationAccount> findByAccount_UuidAndRole(String accountUuid, OrganizationRole role);
    public List<OrganizationAccount> findByAccount_Uuid(String accountUuid);
    public Optional<OrganizationAccount> findByAccount_UuidAndOrganization_Uuid(String accountUuid,String organizationUuid);
    public List<OrganizationAccount> findByOrganization_UuidAndRole(String organizationId, OrganizationRole role);
}
