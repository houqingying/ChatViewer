import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', () => {
    const token = ref<string>('')
    const user = ref<object>({ })
    const useToken = ref<boolean>(true)

    return { token, user, useToken }
})
