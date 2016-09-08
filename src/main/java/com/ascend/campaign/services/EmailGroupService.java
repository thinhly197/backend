package com.ascend.campaign.services;

import com.ascend.campaign.entities.EmailGroup;
import com.ascend.campaign.exceptions.EmailGroupNotFoundException;
import com.ascend.campaign.repositories.EmailGroupRepo;
import com.ascend.campaign.repositories.EmailRepo;
import com.google.common.collect.Lists;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EmailGroupService {
    @NonNull
    private final EmailGroupRepo emailGroupRepo;

    @NonNull
    private final EmailRepo emailRepo;

    @Autowired
    public EmailGroupService(EmailGroupRepo emailGroupRepo, EmailRepo emailRepo) {
        this.emailGroupRepo = emailGroupRepo;
        this.emailRepo = emailRepo;
    }

    public EmailGroup createEmailGroup(EmailGroup emailGroup) {
        return emailGroupRepo.saveAndFlush(emailGroup);
    }

    public EmailGroup getEmailGroup(Long emailGroupId) {
        EmailGroup emailGroupResult = Optional.ofNullable(emailGroupRepo.findOne(emailGroupId))
                .orElseThrow(EmailGroupNotFoundException::new);

        Long qty = emailRepo.countByEmailGroupId(emailGroupResult.getId());
        emailGroupResult.setQuantity(qty);

        return emailGroupResult;

    }

    @Transactional(rollbackFor = Exception.class)
    public EmailGroup deleteEmailGroup(long emailGroupId) {
        EmailGroup emailGroup = Optional.ofNullable(emailGroupRepo.findOne(emailGroupId))
                .orElseThrow(EmailGroupNotFoundException::new);
        emailGroupRepo.delete(emailGroup);
        emailRepo.deleteByEmailGroupId(emailGroupId);
        return emailGroup;
    }

    public EmailGroup updateEmailGroup(long emailGroupId, EmailGroup emailGroup) {
        EmailGroup foundEmailGroup = Optional.ofNullable(emailGroupRepo.findOne(emailGroupId))
                .orElseThrow(EmailGroupNotFoundException::new);
        foundEmailGroup.setName(emailGroup.getName());
        foundEmailGroup.setDescription(emailGroup.getDescription());
        foundEmailGroup.setUpdatedAt(null);
        return emailGroupRepo.saveAndFlush(foundEmailGroup);
    }

    public Page<EmailGroup> getAllEmailGroup(Integer page, Integer perPage, Sort.Direction direction, String sort,
                                             Long emailGroupID, String emailGroupName) {

        Page<EmailGroup> emailGroupPageResult = emailGroupRepo.findAll(filterEmailGroupCriteria(
                        emailGroupID, emailGroupName),
                genPageRequest(page - 1, perPage, direction, sort));
        emailGroupPageResult.getContent().forEach(emailGroup -> {
            Long qty = emailRepo.countByEmailGroupId(emailGroup.getId());
            emailGroup.setQuantity(qty);
        });

        return emailGroupPageResult;

    }

    private PageRequest genPageRequest(int page, int perPage, Sort.Direction direction, String sort) {
        return new PageRequest(page, perPage, new Sort(direction, sort));
    }

    public Specification<EmailGroup> filterEmailGroupCriteria(Long id,
                                                              String searchEmailGroupName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = Lists.newArrayList();
            if (searchEmailGroupName != null) {
                predicates.add(cb.equal(root.get("name"), searchEmailGroupName));
            }
            if (id != null) {
                predicates.add(cb.equal(root.get("id"), id));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
    }
}
