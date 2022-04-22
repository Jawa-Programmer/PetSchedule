package ru.b19513.pet_schedule.service.impl;

import static ru.b19513.pet_schedule.consts.Consts.GROUP_DELETED;
import static ru.b19513.pet_schedule.consts.Consts.INVITATION_SENDED;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import ru.b19513.pet_schedule.controller.entity.GroupDTO;
import ru.b19513.pet_schedule.controller.entity.StatusDTO;
import ru.b19513.pet_schedule.exceptions.NotFoundException;
import ru.b19513.pet_schedule.exceptions.NotPermittedException;
import ru.b19513.pet_schedule.repository.GroupRepository;
import ru.b19513.pet_schedule.repository.InvitationRepository;
import ru.b19513.pet_schedule.repository.UserRepository;
import ru.b19513.pet_schedule.repository.entity.Group;
import ru.b19513.pet_schedule.repository.entity.Invitation;
import ru.b19513.pet_schedule.service.GroupService;
import ru.b19513.pet_schedule.service.mapper.GroupMapper;

@Service
public class GroupServiceImpl implements GroupService {

    private final GroupMapper groupMapper;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;

    @Autowired
    public GroupServiceImpl(GroupMapper groupMapper, GroupRepository groupRepository, UserRepository userRepository,
            InvitationRepository invitationRepository) {
        this.groupMapper = groupMapper;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
    }

    @Override
    public GroupDTO createGroup(String senderLogin, String name) {
        var user = userRepository.findByLogin(senderLogin).orElseThrow(NotFoundException::new);
        var group = Group.builder()
                .name(name)
                .owner(user)
                .build();
        return groupMapper.entityToDTO(groupRepository.save(group));
    }

    @Override
    public GroupDTO updateGroup(String senderLogin, GroupDTO groupDTO) {
        var group = groupRepository.findById(groupDTO.getId()).orElseThrow(NotFoundException::new);
        if (!group.getOwner().getLogin().equals(senderLogin)) {
            throw new NotPermittedException();
        }
        groupMapper.updateEntity(group, groupDTO);
        return groupMapper.entityToDTO(groupRepository.save(group));
    }

    @Override
    public StatusDTO inviteUser(String senderLogin, long groupId, long userId) {
        var group = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        if (!group.getOwner().getLogin().equals(senderLogin)) // только создатель группы может рассылать приглашения
        {
            throw new NotPermittedException();
        }
        var user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        // Если приглашение уже есть в БД - ничего не поменяется
        var inv = new Invitation(user, group);
        invitationRepository.save(inv);

        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(INVITATION_SENDED)
                .build();
    }

    @Override
    public GroupDTO kickUser(String senderLogin, long groupId, long userId) {
        var group = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        if (!group.getOwner().getLogin().equals(senderLogin)) {
            throw new NotPermittedException();
        }
        var user = userRepository.findById(userId).orElseThrow(NotFoundException::new);
        group.getUsers().remove(user);
        return groupMapper.entityToDTO(groupRepository.save(group));
    }

    @Override
    public StatusDTO deleteGroup(long groupId, String ownerLogin) {
        var group = groupRepository.findById(groupId).orElseThrow(NotFoundException::new);
        if (!group.getOwner().getLogin().equals(ownerLogin)) {
            throw new NotPermittedException();
        }
        groupRepository.delete(group);
        return StatusDTO.builder()
                .status(HttpStatus.OK)
                .description(GROUP_DELETED)
                .build();
    }
}
