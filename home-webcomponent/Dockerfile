FROM node:10-alpine

CMD mkdir /opt/app/home-component
ADD . /opt/app/home-component

WORKDIR /opt/app/home-component

RUN npm install --unsafe-perm

# Build component
RUN npm run build:component

EXPOSE 9082
CMD npm run run:api