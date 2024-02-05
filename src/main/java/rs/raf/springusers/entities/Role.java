package rs.raf.springusers.entities;

public enum Role {
    USER,
    ADMIN,

    CAN_READ,
    CAN_UPDATE,
    CAN_DELETE,
    CAN_CREATE,
    CAN_SEARCH_VACUUM,
    CAN_START_VACUUM,
    CAN_STOP_VACUUM,
    CAN_DISCHARGE_VACUUM,
    CAN_ADD_VACUUM,
    CAN_REMOVE_VACUUMS
}
