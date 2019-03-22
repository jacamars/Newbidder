import hazelcast

config = hazelcast.ClientConfig()
client = hazelcast.HazelcastClient(config)

map = client.get_map("MEMBER").blocking()
for key, value in map:
    print("{} is {}",key,value)
