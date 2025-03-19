<template>
  <a-row
    id="globalHeader"
    style="margin-bottom: 0px"
    align="center"
    :wrap="false"
  >
    <a-col flex="auto">
      <a-menu
        class="a-menu"
        mode="horizontal"
        theme="dark"
        :selected-keys="selectedKeys"
        @menu-item-click="click"
      >
        <a-menu-item key="0" :style="{ padding: 0, marginRight: '38px' }">
          <div class="title-bar">
            <img class="logo" src="../assets/YeziLogo1.png" />
            <div class="name">Yezi OJ</div>
          </div>
        </a-menu-item>

        <a-menu-item v-for="item in visibleRoutes" :key="item.path"
          >{{ item.name }}
        </a-menu-item>
      </a-menu>
    </a-col>
    <a-col flex="100px">
      <div>
        {{ store.state.user?.loginUser?.userName ?? "Unauthenticated" }}
      </div>
    </a-col>
  </a-row>
</template>

<script setup lang="ts">
import { routes } from "../router/routes";
import { useRouter } from "vue-router";
import { computed, ref } from "vue";
import { useStore } from "vuex";
import checkAccess from "@/access/checkAccess";

const router = useRouter();
const store = useStore();
//const loginUser = store.state.user.loginUser;

//filter menu with hideInMenu: ture
const visibleRoutes = computed(() => {
  return routes.filter((item, index) => {
    if (item.meta?.hideInMenu) {
      return false;
    }
    //checkAccess(loginUser,needAccess){return blooean}
    if (
      !checkAccess(store.state.user.loginUser, item?.meta?.access as string)
    ) {
      return false;
    }
    return true;
  });
});

//default homepage
const selectedKeys = ref(["/"]);

router.afterEach((to, from, failure) => {
  selectedKeys.value = [to.path];
});

const click = (key: string) => {
  router.push({
    path: key,
  });
};

/*setTimeout(() => {
  store.dispatch("user/getLoginUser", {
    /!*userName: "yezi",
    userRole: ACCESS_ENUM.ADMIN,*!/
  });
}, 10000);*/
</script>

<style scoped>
.title-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #000; /* 背景改为黑色 */
  padding: 1px;
  position: relative; /* 为伪元素提供定位参考 */
  font-size: 20px;
}

.title-bar::after {
  content: ""; /* 伪元素内容为空 */
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 3px; /* 边框的高度 */
  background: linear-gradient(to right, #fff, #000); /* 渐变效果 */
}

.logo {
  height: 100px;
}

.name {
  color: #42b983;
  margin-left: 10px;
  margin-right: 10px;
}

.a-menu {
  box-sizing: border-box;
  width: 100%;
  padding: 10px;
  background-color: #000000;
}
</style>
