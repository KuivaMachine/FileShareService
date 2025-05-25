package backend.model;

import java.sql.Timestamp;
public class File {
    private final String id;
    private final String userId;
    private final String file_name;
    private final byte[] file_data;
    private final int size;
    private final Timestamp created;
    private final Timestamp last_downloaded;


    public File(String id, String userId, String file_name, byte[] fileData, int size, Timestamp created, Timestamp last_downloaded) {
        this.id = id;
        this.file_data = fileData;
        this.userId = userId;
        this.file_name = file_name;
        this.size = size;
        this.created = created;
        this.last_downloaded = last_downloaded;
    }

    public byte[] getFile_data() {
        return file_data;
    }


    public Timestamp getLast_downloaded() {
        return last_downloaded;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getFile_name() {
        return file_name;
    }

    public int getSize() {
        return size;
    }

}