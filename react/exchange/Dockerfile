# Borrowed from https://medium.com/greedygame-engineering/so-you-want-to-dockerize-your-react-app-64fbbb74c217 
# Stage 1 - the build process
FROM node:8 as react-build
WORKDIR /app
COPY . ./
RUN yarn
COPY . ./
RUN yarn build

# Stage 2 - the production environment
FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=react-build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
