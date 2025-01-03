I've finished the whole thing - i.e. 5.1

Step 3.4: 
At one point configuring the OSPF, I set the 'network 84.0.0.0/8' to the entire subnet.
I found at 3.5 that this leaked into the hosts when doing 'tcpdump'
I tried doing /30 addresses for the actual router networks, but it wasn't working.
Then after reading and ctrl+f 'redistribute', I found I had forgotten to do 'redistribute connected' - after that I set the networks to be /30 allowing OSPF to NOT leak to the hosts.
Step 3.6:
I've decided only the 1ms and 10ms latencies will have their cost modified
