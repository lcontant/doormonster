# First Time Setup
##Note

This is a repo transfer from our private bitbucket repo which explains the short history
## AWS access
Get an AWS key for you storage (You should be able to get away with the free tier for testing)
and replace the values `AWS_ACCESS_KEY_HERE` with you access key and replace the `AWS_SECRET_KEY_HERE` with your key.
They're in `serverdocker\Dockerfile`

## MySQL password

We're not going to give you access to our database so you're going to have to run one locally through a docker container
first step is to pick a password by replacing the `MYSQL_PASSWORD` in docker-compose.yml and in all the `application-*.properties` files

##Stripe
For stripe just setup your test account and use the test key provided. Also be sure to follow the testing documentation https://stripe.com/docs/testing
and replace the value ``STRIPE_KEY`` in all the ``application-*.properties``

#Discord
Get your discord secret from a test application you can create on de dev portal and replace the values 
``DISCORD_SECRET`` ``DISCORD_ID`` in all the ``application-*.properties``


