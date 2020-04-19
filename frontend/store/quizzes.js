export const state = () => ({
  list: []
})

export const actions = {
  all(context) {
    this.$axios.$get('http://localhost:8080/quizzes').then((quizzes) => {
      context.commit('setQuizzes', quizzes)
    })
  },
  add(context, quiz) {
    this.$axios.$post('http://localhost:8080/quizzes', quiz).then((quiz) => {
      context.commit('select', quiz)
      context.dispatch('all')
    })
  },
  delete(context, quiz) {
    if (quiz && quiz.id) {
      this.$axios
        .$delete('http://localhost:8080/quizzes/' + quiz.id)
        .then((quiz) => {
          context.dispatch('all')
        })
    }
  }
}

export const mutations = {
  setQuizzes(state, quizzes) {
    state.list = quizzes
  },
  select(state, quiz) {
    state.selected = quiz
  }
}
