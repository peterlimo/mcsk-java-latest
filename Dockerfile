FROM php:7.4-cli
COPY . /testproject
WORKDIR /testproject
CMD [ "php", "./index.php" ]