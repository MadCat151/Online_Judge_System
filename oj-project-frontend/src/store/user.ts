// initial state
//Unauthenticated [ˌʌnəˈθentɪˌkeɪtɪd]
import { StoreOptions } from "vuex";
import ACCESS_ENUM from "@/access/accessEnum";
import { UserControllerService } from "../../generated";

export default {
  namespaced: true,
  state: () => ({
    loginUser: {
      userName: "Unauthenticated",
    },
  }),
  actions: {
    async getLoginUser({ commit, state }, payload) {
      //
      const res = await UserControllerService.getLoginUserUsingGet();
      //commit("updateUser", payload);
      if (res.code === 0) {
        commit("updateUser", res.data);
      } else {
        commit("updateUser", {
          ...state.loginUser,
          userRole: ACCESS_ENUM.NOT_LOGIN,
        });
      }
    },
  },
  mutations: {
    updateUser(state, payload) {
      state.loginUser = payload;
    },
  },
} as StoreOptions<any>;
