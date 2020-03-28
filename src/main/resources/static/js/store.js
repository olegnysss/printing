import Vue from 'vue'
import Vuex from 'vuex'
import auth from "./components/auth";


Vue.use(Vuex);
export default new Vuex.Store({
    modules: {auth},
    state: {},
    getters: {},
    mutations: {},
    actions: {}
})