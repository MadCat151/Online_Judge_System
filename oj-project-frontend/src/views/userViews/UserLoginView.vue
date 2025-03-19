<template>
  <div id="userLoginView">
    <a-form :model="form" :style="{ width: '600px' }" @submit="handleSubmit">
      <a-form-item field="userAccount" label="Account">
        <a-input
          v-model="form.userAccount"
          placeholder="Please enter User Account"
          :style="{ width: '320px' }"
        >
          <template #prefix>
            <img class="logo" src="@/assets/YeziLogo1.png" alt="logo" />
          </template>
        </a-input>
      </a-form-item>
      <a-form-item
        field="userPassword"
        tooltip="Password must be at least 8 characters long."
        label="Password"
      >
        <a-space direction="vertical" size="large">
          <a-switch v-model="visibility" />
          <a-input-password
            v-model="form.userPassword"
            :visibility="visibility"
            placeholder="Please enter your Password"
            :style="{ width: '320px' }"
            :defaultVisibility="false"
            allow-clear
          />
        </a-space>
        <!--        <a-input-password
                  v-model="form.userPassword"
                  placeholder="please enter your Password"
                />-->
      </a-form-item>

      <a-form-item>
        <a-button html-type="submit">Submit</a-button>
      </a-form-item>
    </a-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import { UserControllerService } from "../../../generated";
import message from "@arco-design/web-vue/es/message";
import { useRouter } from "vue-router";
import store from "@/store";

/**
 * information
 */
const form = reactive({
  userAccount: "",
  userPassword: "",
});

/**
 * submission
 */
const router = useRouter();
const handleSubmit = async () => {
  const response = await UserControllerService.userLoginUsingPost(form);
  if (response.code === 0) {
    //Fetch user information again from the action
    await store.dispatch("user/getLoginUser");
    //back to homepage
    router.push({
      path: "/",
      replace: true,
    });
  } else {
    message.error("Failed: " + response.message);
  }
};

/**
 * visibility state for password input
 */
const visibility = ref(true);
</script>

<style scoped>
.logo {
  height: 36px; /* Set the height to 36px */
  width: auto; /* Automatically adjust width to maintain aspect ratio */
}
</style>
