package org.kwince.contribs.osem.model;

public class Address {
	
    private String streetAddress;
    private String city;
    private String state;
    private String zip;
 
    public Address() {
    }
 
    public Address(final String sa, final String city,
            final String state, final String zip) {
        setStreetAddress(sa);
        setCity(city);
        setState(state);
        setZip(zip);
    }
 
    public final String getCity() {
        return city;
    }
 
    public final void setCity(final String city) {
        this.city = city;
    }
 
    public final String getState() {
        return state;
    }
 
    public final void setState(final String state) {
        this.state = state;
    }
 
    public final String getStreetAddress() {
    	return streetAddress;
    }
 
    public final void setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
    }
 
    public final String getZip() {
        return zip;
    }
 
    public final void setZip(final String zip) {
        this.zip = zip;
    }
}