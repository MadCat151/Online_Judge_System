<template>
  <div id="app">
    <template v-if="route.path.startsWith('/user')">
      <router-view></router-view>
    </template>
    <template v-else>
      <BasicLayout></BasicLayout>
    </template>
  </div>
</template>

<style>
#app {
}
</style>

<script setup lang="ts">
import BasicLayout from "@/layouts/BasicLayout.vue";
import { onMounted } from "vue";
import { useRoute } from "vue-router";

const route = useRoute();

//This is where global initialization functions or globally called once code is placed.
const doInit = () => {
  console.log(
    "This is where global initialization functions or globally called once code is placed."
  );
};

onMounted(() => {
  doInit();
});
</script>
