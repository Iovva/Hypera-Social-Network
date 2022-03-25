package domain;

import java.time.LocalDateTime;

public class FriendshipDTO {

    private Long leftId;
    private Long rightId;

    private LocalDateTime date;
    private String status;

    private String fullName;

    public FriendshipDTO(Long leftId, Long rightId, LocalDateTime date, String status){
        this.leftId = leftId;
        this.rightId = rightId;
        this.date = date;
        this.status = status;
        this.fullName = null;
    }

    public FriendshipDTO(Long leftId, Long rightId, LocalDateTime date, String status, String fullName){
        this.leftId = leftId;
        this.rightId = rightId;
        this.date = date;
        this.status = status;
        this.fullName = fullName;
    }

    public Long getLeftId() {
        return leftId;
    }

    public void setLeftId(Long leftId) {
        this.leftId = leftId;
    }

    public Long getRightId() {
        return rightId;
    }

    public void setRightId(Long rightId) {
        this.rightId = rightId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFullName() {
        return fullName;
    }
}
