<template>
  <div id="basicLayout">
    <a-layout style="min-height: 10vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout-content class="content">
        <router-view />
      </a-layout-content>
      <a-layout-footer class="footer">
        <a href="http://baidu.com"> oj_project by Mingxuan </a>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<style scoped>
#basicLayout {
}

#basicLayout .header {
  /*margin-bottom: 10px;*/
  /*height: 130px;*/
  /*width: 100%;*/
  /*align-items: center;*/
  /*height: auto; !* auto height*!*/
  background: linear-gradient(to right, #fff, #aaa);
}

#basicLayout .content {
  background: linear-gradient(to right, #aaa, #fff);
  margin-bottom: 16px;
  padding: 18px;
}

#basicLayout .footer {
  position: sticky;
  background: #efefef;
  padding: 16px;
  bottom: 0;
  /* text-align: left; */
  text-align: center;
}
</style>
<script setup lang="ts">
import GlobalHeader from "@/components/GlobalHeader.vue";
</script>
