package com.hderma.clinic.domain.irb;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class IrbEmailListService {

    private final IrbEmailListRepository emailListRepository;

    @Transactional
    public IrbDto.EmailLogResponse saveEmailLog(Long irbTestId, List<IrbDto.EmailRecipient> users, String emailType) {
        IrbEmailList entity = IrbEmailList.builder()
                .irbTestId(irbTestId).userList(users).emailType(emailType)
                .build();
        return toDto(emailListRepository.save(entity));
    }

    public IrbDto.EmailLogResponse updateEmailLog(Long irbTestId, List<IrbDto.EmailRecipient> newUsers, String emailType) {
        List<IrbEmailList> existingLogs = emailListRepository.findAllByIrbTestId(irbTestId);

        if (!existingLogs.isEmpty()) {
            IrbEmailList currentLog = existingLogs.get(0);
            List<IrbDto.EmailRecipient> currentUsers = currentLog.getUserList();

            Set<String> currentEmails = currentUsers == null ? new HashSet<>()
                    : currentUsers.stream().map(IrbDto.EmailRecipient::getEmail).collect(Collectors.toSet());
            Set<String> newEmails = newUsers == null ? new HashSet<>()
                    : newUsers.stream().map(IrbDto.EmailRecipient::getEmail).collect(Collectors.toSet());

            if (currentEmails.equals(newEmails)) {
                return toDto(currentLog);
            }
            emailListRepository.deleteAll(existingLogs);
        }
        return saveEmailLog(irbTestId, newUsers, emailType);
    }

    // 신규 수신자만 추려서 반환 (알림 발송 대상 판단용)
    public List<IrbDto.EmailRecipient> getAddedRecipients(Long irbTestId, List<IrbDto.EmailRecipient> newUsers) {
        List<IrbEmailList> existingLogs = emailListRepository.findAllByIrbTestId(irbTestId);
        Set<String> existingEmails = existingLogs.stream()
                .flatMap(log -> log.getUserList() != null ? log.getUserList().stream() : java.util.stream.Stream.empty())
                .map(IrbDto.EmailRecipient::getEmail)
                .collect(Collectors.toSet());

        if (newUsers == null) return List.of();
        return newUsers.stream().filter(u -> !existingEmails.contains(u.getEmail())).collect(Collectors.toList());
    }

    public List<IrbDto.EmailLogResponse> getLogsByTestId(Long irbTestId) {
        return emailListRepository.findAllByIrbTestId(irbTestId).stream().map(this::toDto).toList();
    }

    public void delete(Long irbId) {
        List<IrbEmailList> find = emailListRepository.findAllByIrbTestId(irbId);
        if (!find.isEmpty()) emailListRepository.deleteById(find.get(0).getId());
    }

    private IrbDto.EmailLogResponse toDto(IrbEmailList e) {
        return IrbDto.EmailLogResponse.builder()
                .id(e.getId()).irbTestId(e.getIrbTestId()).userList(e.getUserList())
                .emailType(e.getEmailType()).createdAt(e.getCreatedAt())
                .build();
    }
}