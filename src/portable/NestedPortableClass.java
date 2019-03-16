package portable;

import java.io.IOException;
import java.util.Date;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

/**
 * Sample nested domain class which typically is a property of an outer class.
 * 
 * @author ingemar.svensson
 *
 */
public class NestedPortableClass implements Portable {

    private Date dateProperty;
    private Integer intProperty;
    private Long longProperty;
    private Double doubleProperty;
    private String stringProperty;
    private Boolean booleanProperty;

    @Override
    public void readPortable(PortableReader reader) throws IOException {
        Long datePropertyAsLong = reader.readLong("dateProperty");
        if(datePropertyAsLong != null) {
            dateProperty = new Date(datePropertyAsLong);
        }
        intProperty = reader.readInt("intProperty");
        longProperty = reader.readLong("longProperty");
        doubleProperty = reader.readDouble("doubleProperty");
        if(reader.readBoolean("_has__stringProperty")) {
            stringProperty = reader.readUTF("stringProperty");
        }
        booleanProperty = reader.readBoolean("booleanProperty");
    }

    @Override
    public void writePortable(PortableWriter writer) throws IOException {
        if(dateProperty != null) {
            writer.writeLong("dateProperty", dateProperty.getTime());
        }
        if(intProperty != null) {
            writer.writeInt("intProperty", intProperty);
        }
        if(longProperty != null) {
            writer.writeLong("longProperty", longProperty);
        }
        if(doubleProperty != null) {
            writer.writeDouble("doubleProperty", doubleProperty);
        }
        if(stringProperty != null) {
            writer.writeUTF("stringProperty", stringProperty);
            writer.writeBoolean("_has__stringProperty", true);
        }
        if(booleanProperty != null) {
            writer.writeBoolean("booleanProperty", booleanProperty);
        }
    }

    @Override
    public int getClassId() {
        return 2;
    }

    @Override
    public int getFactoryId() {
        return 1;
    }

    public Date getDateProperty() {
        return dateProperty;
    }

    public void setDateProperty(Date dateProperty) {
        this.dateProperty = dateProperty;
    }

    public Integer getIntProperty() {
        return intProperty;
    }

    public void setIntProperty(Integer intProperty) {
        this.intProperty = intProperty;
    }

    public Long getLongProperty() {
        return longProperty;
    }

    public void setLongProperty(Long longProperty) {
        this.longProperty = longProperty;
    }

    public Double getDoubleProperty() {
        return doubleProperty;
    }

    public void setDoubleProperty(Double doubleProperty) {
        this.doubleProperty = doubleProperty;
    }

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public Boolean getBooleanProperty() {
        return booleanProperty;
    }

    public void setBooleanProperty(Boolean booleanProperty) {
        this.booleanProperty = booleanProperty;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((booleanProperty == null) ? 0 : booleanProperty.hashCode());
        result = prime * result
                + ((dateProperty == null) ? 0 : dateProperty.hashCode());
        result = prime * result
                + ((doubleProperty == null) ? 0 : doubleProperty.hashCode());
        result = prime * result
                + ((intProperty == null) ? 0 : intProperty.hashCode());
        result = prime * result
                + ((longProperty == null) ? 0 : longProperty.hashCode());
        result = prime * result
                + ((stringProperty == null) ? 0 : stringProperty.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        NestedPortableClass other = (NestedPortableClass) obj;
        if (booleanProperty == null) {
            if (other.booleanProperty != null)
                return false;
        } else if (!booleanProperty.equals(other.booleanProperty))
            return false;
        if (dateProperty == null) {
            if (other.dateProperty != null)
                return false;
        } else if (!dateProperty.equals(other.dateProperty))
            return false;
        if (doubleProperty == null) {
            if (other.doubleProperty != null)
                return false;
        } else if (!doubleProperty.equals(other.doubleProperty))
            return false;
        if (intProperty == null) {
            if (other.intProperty != null)
                return false;
        } else if (!intProperty.equals(other.intProperty))
            return false;
        if (longProperty == null) {
            if (other.longProperty != null)
                return false;
        } else if (!longProperty.equals(other.longProperty))
            return false;
        if (stringProperty == null) {
            if (other.stringProperty != null)
                return false;
        } else if (!stringProperty.equals(other.stringProperty))
            return false;
        return true;
    }

}