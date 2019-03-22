FROM node:10-alpine

CMD mkdir /opt/app/project-catalog-component
ADD . /opt/app/project-catalog-component

WORKDIR /opt/app/project-catalog-component

RUN npm install --unsafe-perm

# Build component
RUN npm run build:component

EXPOSE 9085
CMD npm run run:api