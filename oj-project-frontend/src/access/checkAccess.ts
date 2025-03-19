/**
 * @param loginUser
 * @param needAccess Permissions needed to access the page
 * @return boolean
 */
import ACCESS_ENUM from "@/access/accessEnum";

const checkAccess = (loginUser: any, needAccess = ACCESS_ENUM.NOT_LOGIN) => {
  //Get loginUserAccess from loginUser.userRole
  const loginUserAccess = loginUser?.userRole ?? ACCESS_ENUM.NOT_LOGIN;
  //No permission required(Admin privileges)
  if (needAccess === ACCESS_ENUM.NOT_LOGIN) {
    return true;
  }
  //Login required
  if (needAccess === ACCESS_ENUM.USER) {
    //Users have no permission if they are not logged in.
    if (loginUserAccess === ACCESS_ENUM.NOT_LOGIN) {
      return false;
    }
  }
  //Admin privileges required
  if (needAccess === ACCESS_ENUM.ADMIN) {
    if (loginUserAccess !== ACCESS_ENUM.ADMIN) {
      return false;
    }
  }
  return true;
};

export default checkAccess;
