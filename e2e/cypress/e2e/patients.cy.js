describe('Patients spec', () => {
    describe('list of patients', ()=> {
        beforeEach(() => {
            cy.visit('/')
        });

        context('filtering', () => {
            afterEach(() => {
                cy.contains('Reset').click()
            })
            it('displays all the patients by default', ()=>{
                cy.get('tbody').find('tr').should('have.length', 3)
            })
            it('filters by name', () => {
                cy.get('[name="search-query"]').type('Test User 1')
                cy.contains('Search').click()
                cy.get('tbody')
                  .find('tr')
                  .should('have.length', 1)
                  .contains('Test User 1')
            })
            it('filters by oms', () => {
                cy.get('[name="search-query"]').type('321')
                cy.contains('Search').click()
                cy.get('tbody')
                  .find('tr')
                  .should('have.length', 1)
                  .contains('Test User 2')
            })
            it('filters by gender', () => {
                cy.get('[name="gender"]').select("M")
                cy.contains('Search').click()
                cy.get('tbody')
                  .find('tr')
                  .should('have.length', 1)
                  .contains('Test User 1')
            })
            it('filters by date fo birth', () => {
                cy.get('[name="date-of-birth"]').type("1992-01-01")
                cy.contains('Search').click()
                cy.get('tbody')
                  .find('tr')
                  .should('have.length', 1)
                  .contains('Test User 3')
            })
        })

        context('sorting', () => {
            it('sorts by name ascending by default', () => {
                cy.get('tbody')
                  .find('tr')
                  .eq(0)
                  .contains('Test User 1')
                cy.get('tbody')
                  .find('tr')
                  .eq(2)
                  .contains('Test User 3')
            })
            it('sorts by gender', () => {
                cy.get('[name="sort-column"]').select('gender')
                cy.get('tbody').find('tr').eq(0).contains('Test User 1')
                cy.get('[name="sort-order"]').select('desc')
                cy.get('tbody').find('tr').eq(0).contains('Test User 2')
                cy.get('tbody').find('tr').eq(1).contains('Test User 3')
            })
            it('sorts by date of birth', () => {
                cy.get('[name="sort-column"]').select('date-of-birth')
                cy.get('tbody').find('tr').eq(0).contains('Test User 1')
                cy.get('[name="sort-order"]').select('desc')
                cy.get('tbody').find('tr').eq(0).contains('Test User 3')
                cy.get('tbody').find('tr').eq(1).contains('Test User 2')
            })
            it('sorts by name', () => {
                cy.get('[name="sort-column"]').select('name')
                cy.get('tbody').find('tr').eq(0).contains('Test User 1')
                cy.get('[name="sort-order"]').select('desc')
                cy.get('tbody').find('tr').eq(0).contains('Test User 3')
                cy.get('tbody').find('tr').eq(1).contains('Test User 2')
            })
        })
    })

    it('updates patient', () => {
        cy.visit('/')
        cy.contains('Test User 1').click()
        cy.get('[name="name"]').clear().type('Test User 1.1')
        cy.contains('Save').click()
        cy.contains('Patient successfully updated').should('exist')
        cy.contains('Back').click()
        cy.contains('Test User 1.1').click()
        // Revert changes
        cy.get('[name="name"]').clear().type('Test User 1')
        cy.contains('Save').click()
    })

    it('creates and deletes user', () => {
        cy.visit('/patients/new')
        cy.get('#name').type('Test User')
        cy.get('#gender').select('M')
        cy.get('#date-of-birth').type('1990-01-01')
        cy.get('#address').type('Test address')
        cy.get('#oms').type('3216549870123456')
        cy.contains('Save').click()
        cy.contains('Patient successfully created').should('exist')
        cy.visit('/patients')
        cy.contains('Test User').click()
        cy.contains('Delete').click()
    })

    it('displays form validation errors', () => {
        cy.visit('/patients/new')
        cy.contains('Save').click()
        cy.contains('Name is required').should('exist')
        cy.contains('Must be a valid gender').should('exist')
        cy.contains('Date of birth is required').should('exist')
        cy.contains('OMS is required').should('exist')
        cy.get('[name="date-of-birth"]').type('3000-01-01')
        cy.get('[name="oms"]').type('123')
        cy.contains('Save').click()
        cy.contains('Date of birth must be in the past').should('exist')
        cy.contains('OMS should contain 16 digits').should('exist')
    })
})
