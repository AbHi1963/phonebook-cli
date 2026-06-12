package phonebook;

public class Contact {

    private String id;
    private String name;
    private String phone;
    private String email;

    public Contact(String id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public String getId()    { return id; }
    public String getName()  { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }

    public void setName(String name)   { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        String e = (email == null || email.isEmpty()) ? "-" : email;
        return pad(id, 6) + pad(name, 22) + pad(phone, 16) + e;
    }

    private String pad(String val, int width) {
        if (val.length() >= width) return val.substring(0, width);
        StringBuilder sb = new StringBuilder(val);
        while (sb.length() < width) sb.append(' ');
        return sb.toString();
    }
}
