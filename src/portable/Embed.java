package portable;

import java.util.UUID;


import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.nio.serialization.ClassDefinition;
import com.hazelcast.nio.serialization.ClassDefinitionBuilder;

public class Embed {
	static Config config;
	static HazelcastInstance instance1;
	static HazelcastInstance instance2;
	
	public static void main(String []args) throws Exception {
		
		setup();
		
		String key = UUID.randomUUID().toString();
        PortableClass portableClass = new PortableClass();
        portableClass.intProperty = 1234;

        IMap<String, PortableClass> writeMap = instance1.getMap("PortableClassMap");
        writeMap.set(key, portableClass);

        IMap<String, PortableClass> readMap = instance2.getMap("PortableClassMap");
        PortableClass result = readMap.get(key);
        System.out.println(result.intProperty);
	}
	
	public static void setup() {
		   config = new Config();
           config.getSerializationConfig().addPortableFactory(1, new MyPortableFactory());

	        ClassDefinitionBuilder nestedPortableClassBuilder = new ClassDefinitionBuilder(1, 2);
	   /*     nestedPortableClassBuilder.addLongField("dateProperty");
	        nestedPortableClassBuilder.addIntField("intProperty");
	        nestedPortableClassBuilder.addLongField("longProperty");
	        nestedPortableClassBuilder.addDoubleField("doubleProperty");
	        nestedPortableClassBuilder.addUTFField("stringProperty");
	        nestedPortableClassBuilder.addBooleanField("_has__stringProperty");
	        nestedPortableClassBuilder.addBooleanField("booleanProperty");  */
	        ClassDefinition nestedPortableClassDefinition = nestedPortableClassBuilder.build();
	        config.getSerializationConfig().addClassDefinition(nestedPortableClassDefinition);

	        ClassDefinitionBuilder portableClassBuilder = new ClassDefinitionBuilder(1, 1);
	        portableClassBuilder.addLongField("dateProperty");
	        portableClassBuilder.addIntField("intProperty");
	        portableClassBuilder.addLongField("longProperty");
	        portableClassBuilder.addDoubleField("doubleProperty");
	        portableClassBuilder.addUTFField("stringProperty");
	        portableClassBuilder.addBooleanField("_has__stringProperty");
	        portableClassBuilder.addBooleanField("booleanProperty");
	        portableClassBuilder.addPortableField("nestedProperty", nestedPortableClassDefinition);
	        portableClassBuilder.addBooleanField("_has__nestedProperty"); 
	        portableClassBuilder.addPortableArrayField("listProperty", nestedPortableClassDefinition);
	        portableClassBuilder.addBooleanField("_has__listProperty");
	        config.getSerializationConfig().addClassDefinition(portableClassBuilder.build()); 

	        instance1 = Hazelcast.newHazelcastInstance(config);
	        instance2 = Hazelcast.newHazelcastInstance(config);
	}
}
