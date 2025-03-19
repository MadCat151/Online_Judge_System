import { RouteRecordRaw } from "vue-router";
import NoAuthView from "/src/views/NoAuthView.vue";
import ACCESS_ENUM from "@/access/accessEnum";
import UserLayout from "@/layouts/UserLayout.vue";
import UserLoginView from "@/views/userViews/UserLoginView.vue";
import UserRegisterView from "@/views/userViews/UserRegisterView.vue";
import AddQuestionView from "@/views/questionViews/AddQuestionView.vue";
import ManageQuestionView from "@/views/questionViews/ManageQuestionView.vue";
import QuestionsView from "@/views/questionViews/QuestionsView.vue";
import ViewQuestionView from "@/views/questionViews/ViewQuestionView.vue";
import SubmittedQuestionsView from "@/views/questionViews/SubmittedQuestionsView.vue";

export const routes: Array<RouteRecordRaw> = [
  {
    path: "/user",
    name: "User",
    component: UserLayout,
    children: [
      {
        path: "/user/login",
        name: "Login",
        component: UserLoginView,
      },
      {
        path: "/user/register",
        name: "Register",
        component: UserRegisterView,
      },
    ],
    meta: {
      hideInMenu: true,
    },
  },
  {
    path: "/",
    name: "Home",
    component: QuestionsView,
  },
  {
    path: "/questions",
    name: "Questions View (Practice Questions)",
    component: QuestionsView,
  },
  {
    path: "/view/question/:id",
    name: "ViewQuestion",
    component: ViewQuestionView,
    props: true,
    meta: {
      access: ACCESS_ENUM.USER,
      hideInMenu: true,
    },
  },
  {
    path: "/add/question",
    name: "Add Question",
    component: AddQuestionView,
    meta: {
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/update/question",
    name: "Update Question",
    component: AddQuestionView,
    meta: {
      hideInMenu: true,
      access: ACCESS_ENUM.USER,
    },
  },
  {
    path: "/manage/question",
    name: "Manage Questions",
    component: ManageQuestionView,
    meta: {
      access: ACCESS_ENUM.ADMIN,
    },
  },
  {
    path: "/questions_submitList",
    name: "Submitted Answers View",
    component: SubmittedQuestionsView,
  },
  /*{
    path: "/hide",
    name: "Hide",
    component: HomeView,
    meta: {
      hideInMenu: true,
    },
  },*/
  {
    path: "/noAuth",
    name: "No Authority",
    component: NoAuthView,
    meta: {
      hideInMenu: true,
    },
  },
  /*  {
      path: "/admin",
      name: "Administrators only",
      component: AdminView,
      meta: {
        access: ACCESS_ENUM.ADMIN,
      },
    },
    {
      path: "/about",
      name: "About Me",
      //routelevelcode-splitting
      //thisgeneratesaseparatechunk(about.[hash].js)forthisroute
      //whichislazy-loadedwhentherouteisvisited.
      component: () =>
        import(/!*webpackChunkName:"about"*!/ "../views/AboutView.vue"),
    },*/
];
