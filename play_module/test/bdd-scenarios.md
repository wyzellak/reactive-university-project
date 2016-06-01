
# After timeout, partial results should be returned
Given the server is under a heavy load  
And there is a node to assign the work to

When the user wants to calculate index value for a stock
And the operation takes over 5 seconds

Then the user should receive partial results of the operation 
And the rest of the operation should be continued on multiple nodes


# Killing a node should not result in data loss
Given the user wants to calculate index value for a stock
And the server is calculating the results

When the node is interrupted (killed)

Then other nodes should take over the calculation
And the user should not notice anything has happened


# Work should be distributed among free nodes to speed up completion
Given the server is under no load
And there are available nodes to assign the work to

When the user wants to calculate index values for all available stocks

Then the calculations should be split between all the available nodes
And the user should receive results much faster than compared to a single-node case


# Even if there are no nodes available at the moment, the work should wait in a queue and be completed eventually
Given the server is under heavy load
And there is only a single free node to assign the work to

When the user wants to calculate index values for all available stocks
And the calculations are queued up to be executed on a single node 
And that node is killed in the middle of the calculations

Then the work should still be queued for execution
And no completed calculation result should be lost
And the calculations should resume as soon as there is a free node available
And the operation should complete successfully


# If there is a big load and the operation times out, it should be removed from the queue as its results are no longer relevant
Given the server is under heavy load
And there is no available node to assign the work to

When the user wants to calculate all index values for a single stock
And the operation has waited over 5 seconds in the queue
And the calculations never started
And the user is tired of waiting, so the request is no longer relevant

Then the system should time out the request
And return an error 
