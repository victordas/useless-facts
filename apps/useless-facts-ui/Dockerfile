# Step 1: Build the Angular app
FROM node:lts AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only the necessary files first
COPY ../../pnpm-lock.yaml ../../package.json ./

# Install pnpm globally
RUN npm install -g pnpm

# Install dependencies using pnpm
RUN pnpm install

# Copy the rest of the source code for your app
COPY . .

# Build the Angular app for production
RUN pnpm nx build useless-facts-ui

# Step 2: Serve the Angular app with Nginx
FROM nginx:alpine

# Copy the built Angular app from the build container to Nginx
COPY --from=build /app/dist/apps/useless-facts-ui /usr/share/nginx/html

# Expose port 80 for Nginx
EXPOSE 4200

# Start Nginx to serve the app
CMD ["nginx", "-g", "daemon off;"]
