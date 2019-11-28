# create accounts
curl -X PUT -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"client\": \"Test Client 1\", \"accountNumber\": \"1111111111111111\", \"amount\": 100 }" http://localhost:9876/pay/api/account
#acc_dt id
curl -X PUT -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"client\": \"Test Client 2\", \"accountNumber\": \"2222222222222222\", \"amount\": 200 }" http://localhost:9876/pay/api/account
#acc_kt id

# check created accounts
curl http://localhost:9876/pay/api/accountList

# make transfer set id acc_dt & acc_kt
curl -X POST -H "Accept: application/json" -H "Content-Type: application/json" -d "{\"acc_dt\": \"ddff0f8e-13c3-4c97-90b5-d25e3c4d4989\", \"acc_kt\": \"dda5ecd0-c878-4d2c-b6a1-23b2419493e2\", \"amount\": 200 }"  http://localhost:9876/pay/api/transfer