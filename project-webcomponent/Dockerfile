FROM node:10-alpine

CMD mkdir /opt/app/project-component
ADD . /opt/app/project-component

WORKDIR /opt/app/project-component

RUN npm install --unsafe-perm

# Build component
RUN npm run build:component

EXPOSE 9084
CMD npm run run:api