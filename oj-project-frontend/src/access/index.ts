import router from "@/router";
import store from "@/store";
import checkAccess from "@/access/checkAccess";
import ACCESS_ENUM from "@/access/accessEnum";
import AccessEnum from "@/access/accessEnum";

router.beforeEach(async (to, from, next) => {
  let loginUser = store.state.user.loginUser;
  const needAccess = (to.meta?.access as string) ?? ACCESS_ENUM.NOT_LOGIN;
  // If the user has not logged in, log them in automatically.
  if (!loginUser || !loginUser.userRole) {
    // Adding await is to wait until the user successfully logs in before executing the subsequent code.
    await store.dispatch("user/login");
    //update new loginUser
    loginUser = store.state.user.loginUser;
  }
  // page needAccess === ACCESS_ENUM.NOT_LOGIN
  if (needAccess === AccessEnum.NOT_LOGIN) {
    next();
    return;
  }
  // page needAccess === ACCESS_ENUM.USER || ACCESS_ENUM.ADMIN

  // if still has not logged in, send to login page
  if (
    !loginUser ||
    !loginUser.userRole ||
    loginUser.userRole === ACCESS_ENUM.NOT_LOGIN
  ) {
    next(`/user/login?redirect=${to.fullPath}`);
    return;
  }
  //if checkAccess === false, go to login page. path:"/noAuth"
  if (!checkAccess(loginUser, needAccess)) {
    next("/noAuth");
    return;
  }
  next();
});
