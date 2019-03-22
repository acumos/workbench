FROM node:10-alpine

CMD mkdir /opt/app/dashboard-component
ADD . /opt/app/dashboard-component

WORKDIR /opt/app/dashboard-component

RUN npm install --unsafe-perm

# Build component
RUN npm run build:component

EXPOSE 9083
CMD npm run run:api